import { Injectable } from '@angular/core';
import {Http, RequestOptions, BaseRequestOptions, Headers}  from '@angular/http';
import { Config } from './config.service';
import {HttpErrorResponse} from '@angular/common/http';
import { DataSet, BodyDataDataset } from '../models/profile';

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
   * Set DataSets/ create a new dataset
   */
  setDataset(domain:string, body:BodyDataDataset): any{
    return this.http.post(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets/`,body).subscribe(
      data => {
        console.log("Return Data from post(create): " + data);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("Client-side error occured.");
        } else {
          console.log("Server-side error occured.");
        }
      }
    );
  }

  /**
   * Edit DataSets
   */
  editDataset(domain:string, dsID:string, body:BodyDataDataset){
    return this.http.put(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets/${dsID}`,body).subscribe(
      data => {
        console.log("Return Data from put(edit): " + data);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("Client-side error occured.");
        } else {
          console.log("Server-side error occured.");
        }
      }
    );
  }
  
  /**
   * Delete DataSets
   */
  deleteDataset(domain: string, dsID: string){
    return this.http.delete(`${ this.config.get('amUrl') }t/sco.core/dsamgmt/0.0.1/${domain}/datasets/${dsID}`).subscribe(
      data => {
        console.log("Return Data from delete(delete): " + data);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("Client-side error occured.");
        } else {
          console.log("Server-side error occured.");
        }
      }
    );
  }
}