package com.epikwallet.core.payment;

import com.epikwallet.core.exception.Cause;

public interface EpikPayListener {
    void onConnectionSuccess();
    void onConnectionFailed(Cause cause);
    void onPurchasedSuccess();
    void onPurchasedFailed(Cause cause);
}
