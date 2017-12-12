import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';

import { User } from '../models/profile';

@Injectable()
export class UsersService {

  constructor(private http: Http, private config: Config) {}
  
  /**
   * Return ManagerList
   */
  getUsers(domain:string): Promise<User[]> {
    //console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/users`)
    .map(response => response.json() as User[])
    .toPromise();
  }

}
