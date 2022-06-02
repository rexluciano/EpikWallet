package com.epikwallet.core.user;

public interface UserListener {
    void onRetrieveSuccess(String message);
	void onRetrieveFailed(String message);
}
