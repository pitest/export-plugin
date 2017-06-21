package org.pitest.plugin.export;

import java.nio.file.FileSystems;

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.config.ReportOptions;

public class MutantExportFactory implements MutationInterceptorFactory {

  @Override
  public String description() {
    return "Mutant export plugin";
  }

  @Override
  public MutationInterceptor createInterceptor(ReportOptions data,
      ClassByteArraySource source) {
    return new MutantExportInterceptor(FileSystems.getDefault(), source, data.getReportDir());
  }

}
