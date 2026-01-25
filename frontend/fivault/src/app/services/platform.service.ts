import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { Platform } from "../pages/app/platform/platform.models";




interface PlatformCreateResponse {
    platformSlug: string;
}

export interface PlatformCreateResult {
    platformSlug: string;
}

export interface PlatformDTO {
    platformName: string;
    platformSlug: string;
    platformDescription: string;
}
/*
interface PlatformDTO {
    ownerName: string;
    platformName: string;
    platformSlug: string;
    platformDescription: string;
}
interface VisiblePlatformDTO {
    platformDTO: PlatformDTO;
    selfPlatformRoleCode: string;
}

interface AppUserPlatformRoleDTO {
    name: string;
    roleCode: string;
}
interface PlatformListResponse {
    ownedPlatforms: VisiblePlatformDTO[];
    nonOwnedPlatforms: VisiblePlatformDTO[];
}


export interface PlatformListResult {
    myPlatforms: VisiblePlatform[];
    otherPlatforms: VisiblePlatform[];
}

export interface PlatformDetailResponse {
    platformDTO: PlatformDTO;
    platformAppUsers: AppUserPlatformRoleDTO[];
}

export interface PlatformDetailResult {
    platform: Platform;
    userRoles: AppUserPlatformRole[];
}
*/

@Injectable({
    providedIn: 'root'
})
export class PlatformService {

    constructor(private http: HttpClient) {

    }

    create(owner: string, domainSlug: string, platformName: string, description: string): Observable<PlatformCreateResult> {
        return this.http.post<PlatformCreateResponse>(`/domain/${owner}/${domainSlug}/platform/create`,
            {
                platformName: platformName,
                description: description
            }
        ).pipe(
            map(response => ({
                platformSlug: response.platformSlug
            }))
        );
    }

    /*
    list(): Observable<PlatformListResult> {
        return this.http.get<PlatformListResponse>('/platform/list').pipe(
            map(response => ({
                myPlatforms: this.mapVisiblePlatforms(response.ownedPlatforms),
                otherPlatforms: this.mapVisiblePlatforms(response.nonOwnedPlatforms)
            }))
        );
    }


    getDetail(owner: string, slug: string): Observable<PlatformDetailResult> {
        // return this.http.get<Object>(`/platform/${owner}/${slug}`);
        return this.http.get<PlatformDetailResponse>(`/platform/${owner}/${slug}`).pipe(
            map(response => ({
                platform: this.mapPlatform(response.platformDTO),
                userRoles: this.mapUserRoles(response.platformAppUsers)
            }))
        );
    }

    */
    public mapPlatforms(dtos: PlatformDTO[]): Platform[] {
        return dtos.map(dto =>
            this.mapPlatform(dto)
        );
    }

    public mapPlatform(dto: PlatformDTO): Platform {
        return {
            name: dto.platformName,
            slug: dto.platformSlug,
            description: dto.platformDescription
        };
    }
    /*
    private mapPlatformRole(dto: AppUserPlatformRoleDTO): AppUserPlatformRole {
        return {
            name: dto.name,
            roleCode: dto.roleCode
        };
    }

    private mapUserRoles(userRolesDTOs: AppUserPlatformRoleDTO[]): AppUserPlatformRole[] {
        return userRolesDTOs.map(dto => this.mapPlatformRole(dto));
    }
    */
}