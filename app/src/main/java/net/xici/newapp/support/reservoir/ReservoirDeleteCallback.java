package net.xici.newapp.support.reservoir;

public interface ReservoirDeleteCallback {
    public void onSuccess();

    public void onFailure(Exception e);
}
