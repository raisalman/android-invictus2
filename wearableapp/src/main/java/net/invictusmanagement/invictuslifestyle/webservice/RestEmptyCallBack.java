package net.invictusmanagement.invictuslifestyle.webservice;

import okhttp3.ResponseBody;

public interface RestEmptyCallBack<T>  {

    void onResponse(ResponseBody response);

    void onFailure(WSException wse);
}
