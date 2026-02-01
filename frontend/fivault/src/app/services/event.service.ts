import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

export interface EventCreateRequest {
    title: string;
    description: string;
    startTimestamp: string | null;
    endTimestamp: string | null;
}

export interface EventCreateResponse {
    eventId: string;
}

export interface EventCreateResult {
    eventId: string;
}

@Injectable({
    providedIn: 'root'
})
export class EventService {

    constructor(private http: HttpClient) { }

    create(owner: string, domainSlug: string, event: EventCreateRequest): Observable<EventCreateResult> {
        return this.http.post<EventCreateResponse>(
            `/domain/${owner}/${domainSlug}/event/create`,
            event
        ).pipe(
            map(response => ({ eventId: response.eventId }))
        );
    }
}