package com.epikwallet.core.wallet;

public interface EpikWalletListener {
    void onWalletUpdateSuccess();
    void onWalletUpdateFailed(String errorMessage);
}
