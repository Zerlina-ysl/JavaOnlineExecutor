package code.olexecutor.executor.service;

import java.util.List;
import java.util.concurrent.Callable;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.springframework.stereotype.Service;

import code.olexecutor.executor.compile.StringSourceCompiler;
import code.olexecutor.executor.execute.JavaClassExecutor;

@Service
public class ExecuteStringSourceService {
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

            Callable<String> runTask = new Callable<String>(){
                  @Override
                  public String call() throws Exception{
                        return JavaClassExecutor.execute(classBytes,systemIn);
                  }
            };

            return "ide";
      }
}
