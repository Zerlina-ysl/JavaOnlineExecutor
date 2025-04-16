package code.olexecutor.executor.service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.springframework.stereotype.Service;

import code.olexecutor.executor.compile.StringSourceCompiler;
import code.olexecutor.executor.execute.JavaClassExecutor;

@Service
public class ExecuteStringSourceService {

      private static final String WAIT_WARNING = "服务器忙，请稍后提交";

      private static final int N_THREAD = 5;

      private static final ExecutorService pool = new ThreadPoolExecutor(N_THREAD, N_THREAD, 0L, TimeUnit.SECONDS,
                  new ArrayBlockingQueue<>(N_THREAD));

      private static final int RUN_TIME_LIMITED = 15;

      private static final String NO_OUTPUT = "nothing";

      public String execute(String source, String systemIn) {
            // 编译结果收集器
            DiagnosticCollector<JavaFileObject> compileCollector = new DiagnosticCollector<>();

            // 编译源代码
            byte[] classBytes = StringSourceCompiler.compile(source, compileCollector);

            if (classBytes == null) {
                  // 编译未通过
                  List<Diagnostic<? extends JavaFileObject>> compileErr = compileCollector.getDiagnostics();
                  StringBuilder compileErrRes = new StringBuilder();
                  for (Diagnostic diagnostic : compileErr) {
                        compileErrRes.append("Compliation error at");
                        compileErrRes.append(diagnostic.getLineNumber());
                        compileErrRes.append(".");
                        compileErrRes.append(System.lineSeparator());
                  }
                  return compileErrRes.toString();
            }
            // 运行字节码的main方法
            Callable<String> runTask = new Callable<String>() {
                  @Override
                  public String call() throws Exception {
                        return JavaClassExecutor.execute(classBytes, systemIn);
                  }
            };

            Future<String> res = null;
            try {
                  res = pool.submit(runTask);
            } catch (RejectedExecutionException e) {
                  return WAIT_WARNING;
            }

            // 获取运行结果，处理非客户端代码错误
            String runResult;
            try {
                  runResult = res.get(RUN_TIME_LIMITED, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                  runResult = "program interrupted";
            } catch (ExecutionException e) {
                  runResult = e.getCause().getMessage();
            } catch (TimeoutException e) {
                  runResult = "time limit exceeded";
            } finally {
                  res.cancel(true);
            }
            return runResult != null ? runResult : NO_OUTPUT;

      }
}
