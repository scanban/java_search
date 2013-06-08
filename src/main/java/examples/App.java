package examples;

import examples.data.CashAccountStore;

import java.util.Random;

/**
 * Hello world!
 *
 */

// -XX:+AggressiveOpts -XX:CompileThreshold=1000 -XX:-PrintCompilation -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:+UseFastAccessorMethods -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
// -XX:+AggressiveOpts -XX:CompileThreshold=1000 -XX:-PrintCompilation -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:+UseFastAccessorMethods -XX:+UnlockDiagnosticVMOptions -XX:CompileThreshold=1 -XX:+PrintAssembly -XX:CompileCommand=print,*.testMethod -XX:PrintAssemblyOptions=intel
public class App 
{
    public static void main( String[] args ) throws Throwable {
        CashAccountStore store = new CashAccountStore();
        Random random = new Random(8);

/*
        CashAccountStore.CashAccountFinder finder =
                store.getFinder().withCode(1000, 10005).withAmount(100, 1000000).withHeight(10,30);
*/

        CashAccountStore.CashAccountFinder finder = store.getFinder();

        if(random.nextBoolean()) { finder = finder.withAmount(0, 0); }
        if(random.nextBoolean()) { finder = finder.withGender(0, 0); }
        int rr;

        rr = random.nextInt(100);
        if(random.nextBoolean()) { finder = finder.withAge(rr, rr + 5); }

        rr = random.nextInt(30000);
        if(random.nextBoolean()) { finder = finder.withCode(rr, rr + 5); }

        if(random.nextBoolean()) { finder = finder.withHeight(0, 0); }


        System.out.println("Warming up...");
        store.find2(finder);
        System.out.println("Running benchmark...");
        long millis = System.currentTimeMillis();
        int i = store.find2(finder);
        long endMillis = System.currentTimeMillis();
        System.out.println("Number of records matched:" + i);
        System.out.println("Elapsed time:" + (endMillis - millis) + "ms");
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "MB");
    }
}
