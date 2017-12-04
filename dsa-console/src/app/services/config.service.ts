import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { environment } from '../../environments/environment';

@Injectable()
export class Config {

    private config: Object = null;
    private env:    Object = null;

    constructor() {}

    /**
     * Return the environment property specified by key
     * @param key
     */
    public get(key: string): any {
        return environment[key];
    }
}
