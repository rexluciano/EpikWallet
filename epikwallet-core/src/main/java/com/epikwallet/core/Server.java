package com.epikwallet.core;

public class Server {
    public static final String DOMAIN_NAME                  =    "https://europe-central.epik.gq"; //Europe Data Center to support international and local server host.
    public static final String END_POINT_LOGIN              =    "/api/v1/login"; //API end point for Login.
    public static final String END_POINT_SIGNUP             =    "/api/v1/signup"; //API end point for registering user.
    public static final String END_POINT_USER               =    "/api/v1/user"; //API end point to retrieve user info.
    public static final String END_POINT_BALANCE            =    "/api/v1/balance"; //To get current user balance.
    public static final String END_POINT_UPDATE_BALANCE     =    "/api/v1/updateWallet"; //To update user balance: DEPRECIATED
    public static final String END_POINT_APPLICATIONS       =    "/api/v1/applications"; //To connect to user application registered in Epik Network.
    public static final String END_POINT_MERCHANT           =    "/api/v1/merchant"; //For merchant only.
    public static final String END_POINT_PURCHASE           =    "/api/v1/pay"; //To send money to other user.
    public static final String END_POINT_WEB_API            =    "/api/v1/connect"; //To connect to the server.
    public static final String CLOUD_STORAGE_URL            =    "https://s3.epik.gq"; //For storage of EpikCloud.
    public static final boolean isDebug                     =    false; //For debugging use only.
}
