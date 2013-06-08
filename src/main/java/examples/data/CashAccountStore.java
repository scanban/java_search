package examples.data;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.*;

abstract class GenMatcherBase {
    abstract boolean c(CashAccountRow row);
}

public class CashAccountStore {
    private static final int ROW_COUNT = 10000000;
    private final long[] accountRows = new long[ROW_COUNT];
    private static Map<String, GenMatcherBase> classMapper = new HashMap<String, GenMatcherBase>();


    public CashAccountStore() {
        Random rand = new Random(100);
        CashAccountRow c = new CashAccountRow();

        for(int i = 0; i < ROW_COUNT; ++i) {
            c.setBitStorage(0L);
            c.setAge(rand.nextInt(100));
            c.setAmount(rand.nextInt(1000) * rand.nextInt(1000));
            c.setCode(rand.nextInt(1000) * rand.nextInt(1000));
            c.setGender(rand.nextInt(2));
            c.setHeight(rand.nextInt(300));
            accountRows[i] = c.getBitStorage();
        }
    }

    //
    // Without online code generation
    //

    public final int find(final CashAccountFinder finder) {
        int rValue = 0;
        CashAccountRow c = new CashAccountRow();

        finder.compileList();
        for(int i = 0; i < ROW_COUNT; ++i) {
            if(finder.isMatched(c.setBitStorage(accountRows[i]))) { ++rValue; }
        }

        return rValue;
    }

    public final int find2(final CashAccountFinder finder) throws Throwable{

        finder.compileList();
        StringBuilder cname = new StringBuilder();
        for(CashAccountFinder.PredicateHolder p: finder.predicateHolderArray) {
            cname.append(p.field.toString());
        }

        GenMatcherBase matcherBase;
        if(classMapper.containsKey(cname.toString())) {
            matcherBase = classMapper.get(cname.toString());
        } else {
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ClassClassPath(this.getClass()));
            CtClass base = classPool.get("examples.data.GenMatcherBase");
            CtClass gen = classPool.makeClass("examples.data.GenMatcher" + cname, base);

            StringBuilder sb = new StringBuilder("public boolean c(examples.data.CashAccountRow r){ return ");
            for(CashAccountFinder.PredicateHolder p: finder.predicateHolderArray) {
                switch (p.field) {
                    case AGE:
                        sb.append("r.getAge() >= " + p.minValue + " && r.getAge() <= " + p.maxValue + " && ");
                        break;
                    case AMOUNT:
                        sb.append("r.getAmount() >= " + p.minValue + " && r.getAmount() <= " + p.maxValue + " && ");
                        break;
                    case CODE:
                        sb.append("r.getCode() >= " + p.minValue + " && r.getCode() <= " + p.maxValue + " && ");
                        break;
                    case GENDER:
                        sb.append("r.getGender() >= " + p.minValue + " && r.getGender() <= " + p.maxValue + " && ");
                        break;
                    case HEIGHT:
                        sb.append("r.getHeight() >= " + p.minValue + " && r.getHeight() <= " + p.maxValue + " && ");
                        break;
                }
            }
            sb.append("true; }");
            System.out.println("Generated code:");
            System.out.println(sb);
            gen.addMethod(CtMethod.make(sb.toString(), gen));
            matcherBase = (GenMatcherBase) gen.toClass().newInstance();
            classMapper.put(cname.toString(), matcherBase);
        }

        CashAccountRow c = new CashAccountRow();
        int rValue = 0;

        for(int i = 0; i < ROW_COUNT; ++i) {
            if(matcherBase.c(c.setBitStorage(accountRows[i]))) { ++rValue; }
        }

    return rValue;
    }

    public CashAccountFinder getFinder() { return new CashAccountFinder(); }

    public static class CashAccountFinder {
        enum RecordFields { AGE, AMOUNT, CODE, GENDER, HEIGHT }
        Map<RecordFields, PredicateHolder> predicates =
                new EnumMap<RecordFields, PredicateHolder>(RecordFields.class);
        PredicateHolder[] predicateHolderArray;

        private CashAccountFinder() {}

        private static int getSelectivety(RecordFields field, int min, int max) {
            int cardinality = 0;

            switch (field) {
                case AGE:
                    cardinality = 100;
                    break;
                case AMOUNT:
                    cardinality = 1000000;
                    break;
                case CODE:
                    cardinality = 1000000;
                    break;
                case GENDER:
                    cardinality = 1;
                    break;
                case HEIGHT:
                    cardinality = 300;
                    break;
            }
            return ROW_COUNT * (1 + max - min) / cardinality;
        }

        private void compileList() {
            predicateHolderArray = predicates.values().toArray(new PredicateHolder[0]);
            Arrays.sort(predicateHolderArray);
/*
            for (PredicateHolder p: predicateHolderArray) {
                System.out.println(p.field + ">>" + p.selectivety);
            }
*/
        }

        public final boolean isMatched(final CashAccountRow row) {
            for(PredicateHolder p: predicateHolderArray) {
                if(p.isInRange(row)) { continue; }
                return false;
            }
            return true;
        }

        public CashAccountFinder withAge(int min, int max) {
            predicates.put(RecordFields.AGE, new PredicateHolder(RecordFields.AGE, min, max));
            return this;
        }

        public CashAccountFinder withAmount(int min, int max) {
            predicates.put(RecordFields.AMOUNT, new PredicateHolder(RecordFields.AMOUNT, min, max));
            return this;
        }

        public CashAccountFinder withCode(int min, int max) {
            predicates.put(RecordFields.CODE, new PredicateHolder(RecordFields.CODE, min, max));
            return this;
        }

        public CashAccountFinder withGender(int min, int max) {
            predicates.put(RecordFields.GENDER, new PredicateHolder(RecordFields.GENDER, min, max));
            return this;
        }

        public CashAccountFinder withHeight(int min, int max) {
            predicates.put(RecordFields.HEIGHT, new PredicateHolder(RecordFields.HEIGHT, min, max));
            return this;
        }

        private abstract static class FieldGetter {
            abstract int getField(CashAccountRow row);
        }

        private static final class AgeFieldGetter extends FieldGetter {
            @Override
            public int getField(final CashAccountRow row) { return row.getAge(); }
        }

        private static final class AmountFieldGetter extends FieldGetter {
            @Override
            public int getField(final CashAccountRow row) { return row.getAmount(); }
        }

        private static final class CodeFieldGetter extends FieldGetter {
            @Override
            public int getField(final CashAccountRow row) { return row.getCode(); }
        }

        private static final class GenderFieldGetter extends FieldGetter {
            @Override
            public int getField(final CashAccountRow row) { return row.getGender(); }
        }

        private static final class HeightFieldGetter extends FieldGetter {
            @Override
            public int getField(final CashAccountRow row) { return row.getHeight(); }
        }

        private final static class PredicateHolder implements Comparable<PredicateHolder> {
            private int minValue;
            private int maxValue;
            private FieldGetter fieldGetter;
            private int selectivety;
            private RecordFields field;

            private PredicateHolder(RecordFields field, int minValue, int maxValue) {
                this.minValue = minValue;
                this.maxValue = maxValue;
                this.field = field;
                this.selectivety = getSelectivety(field, minValue, maxValue);
                //System.out.println(field.toString() + " " + selectivety );


                switch (field) {
                    case AGE:
                        fieldGetter = new AgeFieldGetter();
                        break;
                    case AMOUNT:
                        fieldGetter = new AmountFieldGetter();
                        break;
                    case CODE:
                        fieldGetter = new CodeFieldGetter();
                        break;
                    case GENDER:
                        fieldGetter = new GenderFieldGetter();
                        break;
                    case HEIGHT:
                        fieldGetter = new HeightFieldGetter();
                        break;
                }

            }

            private boolean isInRange(final CashAccountRow row) {
                int value = fieldGetter.getField(row);
                return value >= this.minValue && value <= this.maxValue;
            }

            @Override
            public int compareTo(PredicateHolder o) {
                return this.selectivety - o.selectivety;
            }
        }
    }


}
