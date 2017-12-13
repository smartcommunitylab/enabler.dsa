import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';

import { DataSet, BodyData } from '../models/profile';

@Injectable()
export class DatasetsService {

  constructor(private http: Http, private config: Config) {}

  /**
   * Return DataSets
   */
  getDataSets(domain:string): Promise<DataSet[]> {
    //console.log("come in getDataSets",domain);
    return this.http.get(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets`)
    .map(response => response.json() as DataSet[])
    .toPromise();
  }
  /**
   * Set DataSets
   */
  setDataset(domain:string, body:BodyData): any{
    //return this.http.put(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets/${dsId}`,body);
    return this.http.post(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets/`,body);
  }
}