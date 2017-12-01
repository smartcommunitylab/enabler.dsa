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
