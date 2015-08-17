package net.xici.newapp.support.reservoir;

public interface ReservoirPutCallback {
    public void onSuccess();

    public void onFailure(Exception e);
}
