package examples.data;

import java.util.*;

public class CashAccountStore {
    private static final int ROW_COUNT = 10000000;
    private final long[] accountRows = new long[ROW_COUNT];


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

    public final int find(final CashAccountFinder finder) {
        int rValue = 0;
        CashAccountRow c = new CashAccountRow();

        finder.compileList();
        for(int i = 0; i < ROW_COUNT; ++i) {
            if(finder.isMatched(c.setBitStorage(accountRows[i]))) { ++rValue; }
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

            private PredicateHolder(RecordFields field, int minValue, int maxValue) {
                this.minValue = minValue;
                this.maxValue = maxValue;
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
