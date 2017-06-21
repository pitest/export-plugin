package org.pitest.plugin.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.FileSystem;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.functional.predicate.True;
import org.pitest.mutationtest.build.ClassTree;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.gregor.GregorMutater;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.config.Mutator;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class MutantExportInterceptorTest {
  
  ClassloaderByteArraySource source = ClassloaderByteArraySource.fromContext();
  MutantExportInterceptor testee;
  FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
  GregorMutater mutator;
  
  @Before
  public void setUp() {
    Collection<MethodMutatorFactory> mutators = Mutator.defaults();
    mutator = new GregorMutater(source, True.<MethodInfo>all(), mutators);
    testee = new MutantExportInterceptor(fileSystem, null, "target");
  }

  @Test
  public void shouldCreateAMutantsDirectoryForEachClass() {
    testee.begin(tree(Foo.class));
    testee.begin(tree(String.class));
    assertThat(fileSystem.getPath("target", "org", "pitest", "plugin", "export", "Foo", "mutants")).exists();
    assertThat(fileSystem.getPath("target", "java", "lang", "String", "mutants")).exists();
  }

  @Test
  public void shouldReturnMutantListUnmodified() {
    Collection<MutationDetails> mutations = mutator.findMutations(ClassName.fromClass(VeryMutable.class));
   
    testee.begin(tree(VeryMutable.class));
    Collection<MutationDetails> actual = testee.intercept(mutations, mutator);
    testee.end();
    
    assertThat(actual).isSameAs(mutations);
  }

  @Test
  public void shouldWriteAllMutantBytesToDisk() {
    Collection<MutationDetails> mutations = mutator.findMutations(ClassName.fromClass(VeryMutable.class));
    
    testee.begin(tree(VeryMutable.class));
    testee.intercept(mutations, mutator);
    testee.end();
    
    Mutant firstMutant = mutator.getMutation(mutations.iterator().next().getId());
    assertThat(fileSystem.getPath("target",firstMutantPath(VeryMutable.class))).hasBinaryContent(firstMutant.getBytes());
  }
  
  
  private String[] firstMutantPath(Class<?> clazz) {
    ClassName name = ClassName.fromClass(clazz);
    return (name.asInternalName() + "/mutants/0/" + name.asJavaName() + ".class").split("/");
  }

  private ClassTree tree(Class<?> clazz) {
    return ClassTree.fromBytes(source.getBytes(clazz.getName()).value());
  }
}


class Foo {
  
}


class VeryMutable {
  public int foo(int i) {
    for (int y = 0; y != i; y++) {
      System.out.println("" + (i * y));
    }
    return i + 2;
  }
}