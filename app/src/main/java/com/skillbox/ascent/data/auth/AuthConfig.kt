package com.skillbox.ascent.data.auth

import net.openid.appauth.ResponseTypeValues


object AuthConfig {
    const val AUTH_URI = "https://www.strava.com/oauth/authorize"
    const val TOKEN_URI = "https://www.strava.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "activity:read_all,activity:write,profile:read_all,profile:write"
    const val PROMPT = "auto"

    const val CLIENT_ID = "69449"
    const val CLIENT_SECRET = "829030ecf399b16bfc2f82a48151564a91a7c2d5"
    const val CALLBACK_URL = "ascent://com.skillbox.ascent"

    const val REFRESH_TOKEN = "refresh_token"
}