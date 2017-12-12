import { Injectable }                   from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';

import { UserProfile } from '../models/profile';

@Injectable()
export class DefaultRequestOptions extends BaseRequestOptions {

  constructor() {
    super();
    this.headers.set('Accept', 'application/json');
    this.headers.set('Content-Type', 'application/json');
    this.headers.set('Authorization', `Bearer ${sessionStorage.getItem('access_token')}`);
  }
}

export const requestOptionsProvider = { provide: RequestOptions, useClass: DefaultRequestOptions };

export enum LOGIN_STATUS {
      NOTSIGNEDIN,
      NEW,
      EXISTING
}

@Injectable()
export class DataService  {

  constructor(private http: Http, private config: Config) {}

  /**
   * Return user profile
   */
  getUserProfile(): Promise<UserProfile> {
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/profile`)
    .map(response => response.json() as UserProfile)
    .toPromise();
  }

}
