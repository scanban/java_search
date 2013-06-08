package examples.data;

public final class CashAccountRow {

    private enum shifts  {
        CODE(0), GENDER(20), AGE(21), AMOUNT(28), HEIGHT(48);

        private final int v;
        private shifts(int value) { this.v = value; }
    }

    private enum masks  {
        CODE(0x0fffffL),
        GENDER(1L << shifts.GENDER.v),
        AGE(0x07fL << shifts.AGE.v),
        AMOUNT(0x0fffffL << shifts.AMOUNT.v),
        HEIGHT(0x01ffL << shifts.HEIGHT.v);

        private final long v;
        private masks(long value) { this.v = value; }
    }


    private long bitStorage;

    long getBitStorage() { return bitStorage; }
    CashAccountRow setBitStorage(long bitStorage) {
        this.bitStorage = bitStorage;
        return this;
    }

    public int getCode() { return (int) (bitStorage & masks.CODE.v); }
    public void setCode(int code) {
        bitStorage = (bitStorage & ~masks.CODE.v) | ((long) code & masks.CODE.v);
    }

    public int getGender() { return (int) ((bitStorage & masks.GENDER.v) >>> shifts.GENDER.v ); }
    public void setGender(int gender) {
        bitStorage = (bitStorage & ~masks.GENDER.v) | ((long) gender << shifts.GENDER.v & masks.GENDER.v);
    }

    public int getAge() { return (int) ((bitStorage & masks.AGE.v) >>> shifts.AGE.v ); }
    public void setAge(int age) {
        bitStorage = (bitStorage & ~masks.AGE.v) | ((long) age << shifts.AGE.v & masks.AGE.v);
    }

    public int getAmount() { return (int) ((bitStorage & masks.AMOUNT.v) >>> shifts.AMOUNT.v ); }
    public void setAmount(int amount) {
        bitStorage = (bitStorage & ~masks.AMOUNT.v) | ((long) amount << shifts.AMOUNT.v & masks.AMOUNT.v);
    }

    public int getHeight() { return (int) ((bitStorage & masks.HEIGHT.v) >>> shifts.HEIGHT.v ); }
    public void setHeight(int height) {
        bitStorage = (bitStorage & ~masks.HEIGHT.v) | ((long) height << shifts.HEIGHT.v & masks.HEIGHT.v);
    }


}
