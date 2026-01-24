import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { Domain } from "../pages/app/domain/domain.models";


interface DomainCreateResponse { }

export interface DomainCreateResult { }
interface VisibleDomainDTO {
    ownerName: string;
    domainName: string;
    domainSlug: string;
    domainDescription: string;
    selfDomainRoleCode: string;
}

interface DomainListResponse {
    ownedDomains: VisibleDomainDTO[];
    nonOwnedDomains: VisibleDomainDTO[];
}


export interface DomainListResult {
    myDomains: Domain[];
    otherDomains: Domain[];
}

@Injectable({
    providedIn: 'root'
})
export class DomainService {

    constructor(private http: HttpClient) {

    }

    create(domainName: string, description: string): Observable<DomainCreateResult> {
        return this.http.post<DomainCreateResponse>('/domain/create',
            {
                domainName: domainName,
                description: description
            }
        );
    }

    list(): Observable<DomainListResult> {
        return this.http.get<DomainListResponse>('/domain/list').pipe(
            map(response => ({
                myDomains: this.mapDomains(response.ownedDomains),
                otherDomains: this.mapDomains(response.nonOwnedDomains)
            }))
        );
    }

    private mapDomains(dtos: VisibleDomainDTO[]): Domain[] {
        return dtos.map(dto => ({
            owner: dto.ownerName,
            name: dto.domainName,
            slug: dto.domainSlug,
            description: dto.domainDescription,
            roleCode: dto.selfDomainRoleCode
        }));
    }
}