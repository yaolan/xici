package net.xici.newapp.support.reservoir;

public interface ReservoirGetCallback<T> {
    public void onSuccess(T object);

    public void onFailure(Exception e);
}
