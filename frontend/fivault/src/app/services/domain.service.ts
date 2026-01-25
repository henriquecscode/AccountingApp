import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { AppUserDomainRole, Domain, VisibleDomain } from "../pages/app/domain/domain.models";


interface DomainCreateResponse { }

export interface DomainCreateResult { }

interface DomainDTO {
    ownerName: string;
    domainName: string;
    domainSlug: string;
    domainDescription: string;
}
interface VisibleDomainDTO {
    domainDTO: DomainDTO;
    selfDomainRoleCode: string;
}

interface AppUserDomainRoleDTO {
    name: string;
    roleCode: string;
}
interface DomainListResponse {
    ownedDomains: VisibleDomainDTO[];
    nonOwnedDomains: VisibleDomainDTO[];
}


export interface DomainListResult {
    myDomains: VisibleDomain[];
    otherDomains: VisibleDomain[];
}

export interface DomainDetailResponse {
    domainDTO: DomainDTO;
    domainAppUsers: AppUserDomainRoleDTO[];
}

export interface DomainDetailResult {
    domain: Domain;
    userRoles: AppUserDomainRole[];
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
                myDomains: this.mapVisibleDomains(response.ownedDomains),
                otherDomains: this.mapVisibleDomains(response.nonOwnedDomains)
            }))
        );
    }


    getDetail(owner: string, slug: string): Observable<DomainDetailResult> {
        // return this.http.get<Object>(`/domain/${owner}/${slug}`);
        return this.http.get<DomainDetailResponse>(`/domain/${owner}/${slug}`).pipe(
            map(response => ({
                domain: this.mapDomain(response.domainDTO),
                userRoles: this.mapUserRoles(response.domainAppUsers)
            }))
        );
    }


    private mapVisibleDomains(dtos: VisibleDomainDTO[]): VisibleDomain[] {
        return dtos.map(dto => ({
            domain: this.mapDomain(dto.domainDTO),
            roleCode: dto.selfDomainRoleCode
        }));
    }

    private mapDomain(dto: DomainDTO): Domain {
        return {
            owner: dto.ownerName,
            name: dto.domainName,
            slug: dto.domainSlug,
            description: dto.domainDescription
        };
    }

    private mapDomainRole(dto: AppUserDomainRoleDTO): AppUserDomainRole {
        return {
            name: dto.name,
            roleCode: dto.roleCode
        };
    }

    private mapUserRoles(userRolesDTOs: AppUserDomainRoleDTO[]): AppUserDomainRole[] {
        return userRolesDTOs.map(dto => this.mapDomainRole(dto));
    }
}