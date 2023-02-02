package com.cjs.sso.qw;

public interface QWService {
    String getuserinfo(String code);

    String getCurrentUser(String current);
}
