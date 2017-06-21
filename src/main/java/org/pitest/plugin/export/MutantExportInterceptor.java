package org.pitest.plugin.export;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.build.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

public class MutantExportInterceptor implements MutationInterceptor {

  private final String outDir;
  private final FileSystem fileSystem;
  
  private Path currentClassDir;
  private ClassName currentClass;

  public MutantExportInterceptor(FileSystem fileSystem, ClassByteArraySource source, String outDir) {
    this.fileSystem = fileSystem;
    this.outDir = outDir;
  }

  @Override
  public InterceptorType type() {
    return InterceptorType.REPORT;
  }

  
  @Override
  public void begin(ClassTree clazz) {
    currentClass = clazz.name();
    String[] classLocation = (clazz.name().asJavaName() + ".mutants").split("\\.");
    currentClassDir = fileSystem.getPath(outDir, classLocation);
    try {
      Files.createDirectories(currentClassDir);
    } catch (IOException e) {
      throw new RuntimeException("Couldn't create direectory for " + clazz, e);
    }
  }

  @Override
  public Collection<MutationDetails> intercept(
      Collection<MutationDetails> mutations, Mutater m) {
    
    List<MutationDetails> indexable = new ArrayList<>(mutations);
    
    try {
    for (int i = 0; i != indexable.size(); i++ ) {
      writeMutantToDisk(m, indexable, i);
    }
    } catch(IOException ex) {
      throw new RuntimeException("Error exporting mutants for report", ex);
    }
    
    return mutations;
  }

  private void writeMutantToDisk(Mutater m, List<MutationDetails> indexable,
      int i) throws IOException {
    MutationDetails md = indexable.get(i);
    Path mutantFolder = currentClassDir.resolve("" + i);
    Files.createDirectories(mutantFolder);
    Path outFile = mutantFolder.resolve(currentClass.asJavaName() + ".class");
    
    Mutant mutant = m.getMutation(md.getId());
    
    Files.write(outFile, mutant.getBytes(), StandardOpenOption.CREATE);
  }
  
  
  @Override
  public void end() {

  }
  
  
//  /**
//   * Writes a mutant's byte array to a file. The class name which contains the
//   * package name will be used to create a folder structure in the target
//   * directory. The dir_pre parameter is used to create a directory in the
//   * target directory so that mutants with same class names can be written to
//   * the target directory in different directories. E.g. a class with name
//   * "foo.Bar", when dir_pre is defined as "pre" will end up in
//   * "_targetDir_/pre/foo/Bar.class"
//   * 
//   * @param className
//   *            The name of the class that is written to file.
//   * @param mutant
//   *            The byte array representing the mutant
//   * @param dirPre
//   *            A directory that can be made to put the mutant in, which makes
//   *            it easier to write multiple mutants in the same directory
//   * @throws FileNotFoundException
//   * @throws IOException
//   */
//  public void writeMutantClassToFile(String className, byte[] mutant, String dirPre)
//      throws FileNotFoundException, IOException {
//
//    String[] dirs = className.split("\\.");
//    String reldir = this.targetDir;
//    if (dirPre != null && dirPre != "") {
//      File dir = new File(reldir, dirPre);
//      dir.mkdir();
//      reldir = dir.getAbsolutePath();
//    }
//    for (int i = 0; i < dirs.length - 1; i++) {
//      File dir = new File(reldir, dirs[i]);
//      if (!dir.exists()) {
//        dir.mkdir();
//      }
//      reldir += File.separatorChar + dir.getName();
//    }
//
//    File file = new File(reldir, dirs[dirs.length - 1] + ".class");
//
//    if (!file.exists())
//      file.createNewFile();
//
//    FileOutputStream fstream = new FileOutputStream(file);
//    fstream.write(mutant);
//    fstream.close();
//  }



}


