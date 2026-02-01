import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventCreateRequest {
    title: string;
    description: string;
    startTimestamp: string | null;
    endTimestamp: string | null;
}

export interface EventCreateResponse {
    eventId: string;
}

@Injectable({
    providedIn: 'root'
})
export class EventService {

    constructor(private http: HttpClient) { }

    create(owner: string, domainSlug: string, event: EventCreateRequest): Observable<EventCreateResponse> {
        return this.http.post<EventCreateResponse>(
            `/api/domain/${owner}/${domainSlug}/event`,
            event
        );
    }
}