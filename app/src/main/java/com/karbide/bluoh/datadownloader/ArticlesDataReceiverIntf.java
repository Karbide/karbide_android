package com.karbide.bluoh.datadownloader;

import android.os.Bundle;

/**
 * Created by cheta on 29-12-2016.
 */

public interface ArticlesDataReceiverIntf extends DataReceiverIntf{
    public void onReceiveResult(int resultCode, Bundle resultData);
}