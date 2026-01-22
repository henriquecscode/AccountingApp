import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";


interface Domain {

}
interface DomainCreateResponse {
    domain: Domain;
}
@Injectable({
    providedIn: 'root'
})
export class DomainService {

    constructor(private http: HttpClient) {

    }

    create(domainName: string, description: string): Observable<DomainCreateResponse> {
        return this.http.post<DomainCreateResponse>('/domain/create',
            {
                domainName: domainName,
                description: description
            }
        );
    }
}