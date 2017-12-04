<<<<<<< HEAD
import { Injectable } from '@angular/core';

@Injectable()
export class LoginService {

  constructor() { }

=======
import { Injectable }                   from '@angular/core';
import {Http, RequestOptions, Headers}  from '@angular/http';
import { Config }                       from './config.service';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';

declare var window: any;


@Injectable()
export class LoginService  {
  isLoggedIn = false;

  constructor(private http: Http, private config: Config) {
  }

  login(): void {
    window.location = `${ this.config.get('aacUrl') }/eauth/authorize/google?client_id=${ this.config.get('aacClientId') }&redirect_uri=${ this.config.get('redirectUrl') }&response_type=token&scope=${ this.config.get('scope') }`;
  }

  /**
   * Logout the user from the portal and, if specified, also from the provider
   */
  logout(logoutProvider?: boolean): Promise<boolean> {
    // TODO: revoke token and return true
    sessionStorage.clear();
    this.isLoggedIn = false;
    this.serverLogout(logoutProvider);
    return Promise.resolve(true);
  }

  serverLogout(logoutProvider?: boolean): void {
    let redirect = null;
    if (logoutProvider) {
    // SIGNOUT FROM GOOGLE
      redirect = this.config.get('aacUrl') + '/logout?target=https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=' + window.location.href;
    } else {
      redirect = this.config.get('aacUrl') + '/logout?target=' + window.location.href;
    }
    window.location = redirect;
  }

  /**
   * Check status of the login. Promise true if the user is already logged or the token present in storage is valid
   */
  checkLoginStatus(): Promise<boolean> {
    if (!sessionStorage.access_token) {
      if (this.captureOauthToken()) {
        window.location.hash = '/';
        window.location.reload();
        // this is useless, for consistency
        return Promise.resolve(true);
      }
      return Promise.resolve(false);
    }
    return new Promise((resolve, reject) => {
      if (this.isLoggedIn) {
        resolve(true);
        return;
      }
      this.checkTokenInfo(sessionStorage.access_token, this.config.get('scope'))
      .then(valid => {
        if (!valid) {
          sessionStorage.clear();
          resolve(false);
        } else {
          this.isLoggedIn = true;
          resolve(true);
        }
      },
      err => {
        this.isLoggedIn = false;
        sessionStorage.clear();
        resolve(false);
      });
    });
  }

  /**
   * Check whether token is valid
   */
  private checkTokenInfo(token: string, scope: string): Promise<boolean> {
    return this.http.get(`${ this.config.get('amUrl') }/aac/1.0.0/resources/token`)
    .map(response => {
      const json = response.json();
      if (!json.valid) { return false; }
      const scopes = scope.split(',');
      let valid = true;
      scopes.forEach(s => valid = valid && json.scope.indexOf(s) >= 0);
      // return valid;
      return true;
    })
    .toPromise();
  }

  /**
   * Try to read access token from hash
   */
  private captureOauthToken() {
    if (!!sessionStorage.access_token && sessionStorage.access_token !== 'null' && sessionStorage.access_token !== 'undefined') {
      return;
    }
    console.log('capture token on index.html');
    const queryString = location.hash.substring(1);
    const params = {};
    const regex = /([^&=]+)=([^&]*)/g;
    let m = null;
    while (m = regex.exec(queryString)) {
      params[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
      // Try to exchange the param values for an access token.
      if (params['access_token']) {
        sessionStorage.access_token = params['access_token'];
        return true;
      }
    }
    return false;
  }
  /**
   * Return AAC access token if present
   */
  getToken(): string {
    return sessionStorage.getItem('access_token');
  }
>>>>>>> e081abef02326bb362233c8283b6aee8c72bf034
}
