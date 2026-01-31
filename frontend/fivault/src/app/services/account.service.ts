import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { Account } from "../pages/app/account/account.model";

interface AccountCreateResponse {
    accountSlug: string;
}

export interface AccountCreateResult {
    accountSlug: string;
}

export interface AccountDTO {
    accountName: string;
    accountSlug: string;
    accountDescription: string;
}

export interface AccountDetailResponse {
    accountDTO: AccountDTO;
    // Add other related DTOs if needed
}

export interface AccountDetailResult {
    account: Account;
    // Add other related data if needed
}

@Injectable({
    providedIn: 'root'
})
export class AccountService {

    constructor(private http: HttpClient) { }

    create(owner: string, domainSlug: string, platformSlug: string, accountName: string, description: string): Observable<AccountCreateResult> {
        return this.http.post<AccountCreateResponse>(
            `/domain/${owner}/${domainSlug}/platform/${platformSlug}/account/create`,
            {
                accountName: accountName,
                description: description
            }
        ).pipe(
            map(response => ({
                accountSlug: response.accountSlug
            }))
        );
    }

    getDetail(owner: string, domainSlug: string, platformSlug: string, accountSlug: string): Observable<AccountDetailResult> {
        return this.http.get<AccountDetailResponse>(
            `/domain/${owner}/${domainSlug}/platform/${platformSlug}/account/${accountSlug}`
        ).pipe(
            map(response => ({
                account: this.mapAccount(response.accountDTO)
            }))
        );
    }

    public mapAccounts(dtos: AccountDTO[]): Account[] {
        return dtos.map(dto =>
            this.mapAccount(dto)
        );
    }

    public mapAccount(dto: AccountDTO): Account {
        return {
            name: dto.accountName,
            slug: dto.accountSlug,
            description: dto.accountDescription
        };
    }
}