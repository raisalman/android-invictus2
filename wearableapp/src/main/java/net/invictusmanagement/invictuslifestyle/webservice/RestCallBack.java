package net.invictusmanagement.invictuslifestyle.webservice;

public interface RestCallBack<T>  {

    void onResponse(T response);

    void onFailure(WSException wse);
}
