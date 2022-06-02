package com.epikwallet.core.auth;

/*Auth Listener*/
public interface EpikAuthListener {
	void onSigninUser(final String message, final boolean isSucess);
	void onCreateUser(final String message, final boolean isSucess);
	void onEmailVerificationSent(final String message, final boolean isSucess);
}
