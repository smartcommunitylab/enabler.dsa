import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';

import { Manager } from '../models/profile';

@Injectable()
export class ManagersService {

  constructor(private http: Http, private config: Config) {}

  /**
   * Return ManagerList
   */
  getManagers(domain:string): Promise<Manager[]> {
    //console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/managers`)
    .map(response => response.json() as Manager[])
    .toPromise();
  }
}

