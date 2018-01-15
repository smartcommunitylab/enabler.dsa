import { Injectable } from '@angular/core';

export class UserProfile {
    username: string;
    displayname: string;
    domains: DomainProfile[];
}

export class DomainProfile {
    domain: string;
    role: string;
    dataset?: string;
}

export class DataSet {
    id: string;
    configuration: Configuration;
}
export class Configuration{
    config:string[];
}

export class Manager {
    id: string;
    email: string;
}

export class User {
    id: string;
    email: string;
    dataset: string;
}
@Injectable()
export class BodyDataDataset {
    //id: string;
    //configuration?: string;
    domain : string;
    dataset : string;
    configurationProperties : {
        indexFormat? : string,
        archivePolicy? : string,
        clients? : [ string ],
        dataMapping? : {}
    }
}

@Injectable()
export class BodyDataManager {
    id?: string;
    email?: string;
    username:string;
    owner:boolean;
    /*
    domain : string;
    dataset : string;
    configurationProperties : {
        indexFormat? : string,
        archivePolicy? : string,
        clients? : [ string ],
        dataMapping? : {}
    }
    */
}

@Injectable()
export class BodyDataUser {
    id?: string;
    username: string;
    dataset?:string;
}

@Injectable()
export class GlobalData {
    id?: string;
    configuration?: string;
    domain?:string;
}