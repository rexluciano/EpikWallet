# EpikWallet API Beta 1.0.0
[![](https://jitpack.io/v/rexllc/EpikWallet.svg)](https://jitpack.io/#rexllc/EpikWallet)

**EpikWallet API** is a library to make your development easy. It's have neat, and good ready made views that you can implement to your app easily with bundled UI from EpikWallet

### Includes:

• EpikAuth Login UI, easily authenticate your user with EpikAuth Login UI.

• EpikPay bundled with EpikPayButton, add payment method easily with EpikPay using EpikPayButton.

## EpikWallet API Implementation for Android:

### Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Add the dependency

```
	dependencies {
	        implementation 'com.github.rexllc:EpikWallet:v1.0.0-beta03'
	}
```

#### Initialize the EpikApp
The most important things you need before all Epik API calls.

>put in onCreate method and must be set it after super.onCreate()

```
EpikApp.initializeApp(this, YOUR_TOKEN_HERE);
```

#### EpikAuth without Login UI.

Initialize EpikAuth instance.

```
EpikAuth mAuth = EpikAuth.getInstance(this);
```

Authenticate the user.

```
mAuth.signinUserWithEmailAndPassword(email, password, listener);
```

Create user account.

Set User Name and Surname first.

```
mAuth.setName("Your Name").setSurname("Your Last Name");
```

Then call:

```
mAuth.createUserWithEmailAndPassword(email, password, listener);
```

Add the EpikAuth Listener.

```
private EpikAuthListener listener = new AuthListener() {
      @Override
      public void onSigninUser(String error, boolean isSuccess) {
      //When the user successfully logged in.
      }
      @Override
      public void onCreateUser(String error, boolean isSuccess) {
      //When the user successfully creates an account.
      }
      @Override
      public void onEmailVerificationSent(String error, boolean isSuccess) {
      //On email verification sent.
      }
};
```

## EpikPay API

An easy ready made Payment Processor API for your apps that use it's own currency called EpikCoin or in other words it's eC.

Create variable.

```
private EpikPay mPay;
```

Initialize EpikPay.

```
mPay = EpikPay.getInstance(this);
```

Connect your EpikPay API key with your EpikWallet Account Number.

```
mPay.connect("API KEY", "YOUR ACCOUNT NUMBER");
```

Add EpikPayButton to your XML layout.

```
   <com.epikwallet.core.ui.button.EpikPayButton
             android:id="@+id/mPayButton"
             android:layout_width="wrap_content"
             android:layout_height="wrap_content" />
```

Add the listeners.

```
mPay.setEpikPayListener(new EpikPayListener() {
       @Override
       public void onConnectionSuccess() {}
       @Override
       public void onConnectionFailed(Cause cause) {}
       @Override
       public void onPurchasedSuccess() {}
       @Override
       public void onPurchasedFailed(Cause cause) {}
});
```

Add Item.

```
mPayButton.setItemName("Name of Item");
mPayButton.setItemValue(0);
```

Custom EpikPay method without a Button UI.

```
//onClick
mPay.setName("Name of Item").setValue(0).launch();
```

## EpikAuth UI

Add authentication to your app with ready made UI's.


Authenticate the user easily.

```
EpikAuthUI.getInstance().signinWithEpik()
   .addOnCompleteListener(new EpikAuthCompleteListener() {
        @Override
        public void onCompleted(String error, boolean isSuccess) {
        //When user successfully logged in.
    }
});
```

To logout the user.

```
EpikAuth.getInstance(this).logout();
```

## Wallet API

Under development API that you can use for now.

Initialize the Wallet.

```
private EpikWallet mWallet;

//onCreate method
mWallet = EpikWallet.getInstance(this);
mWallet.init(); //This will make calls to EpikWallet database to retrieve the user wallet credits.

//To get balance.
String mBalance = mWallet.getBalance();
```

*This library is under development and may break your project. Use it only at your own risk.*
