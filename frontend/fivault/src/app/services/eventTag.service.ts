import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { EventCreateRequest } from "./event.service";
import { map, Observable } from "rxjs";
import { EventTag } from "../pages/app/domain/eventTag/eventTag.model";

interface eventTagDTO {
    eventTagId: string;
    name: string;
    description: string;
    parentEventTagId: string | any

}

interface EventTagCreateResult {
    eventTag: EventTag;
}
interface EventTagCreateResponse {
    eventTagDTO: eventTagDTO;
}

interface EventTagListResult {
    eventTagList: EventTag[]
}
interface EventTagListResponse {
    eventTagDTOList: eventTagDTO[]
}
@Injectable({
    providedIn: 'root'
})
export class EventTagService {

    constructor(private http: HttpClient) { }

    create(owner: string, domainSlug: string, name: string, description: string, parentEventTagId: string | null): Observable<EventTagCreateResult> {
        return this.http.post<EventTagCreateResponse>(
            `/domain/${owner}/${domainSlug}/eventTag/create`,
            {
                name,
                description,
                parentEventTagId: parentEventTagId
            }).pipe(
                map(response => ({
                    eventTag: this.mapEventTag(response.eventTagDTO)
                }))
            );
    }

    getList(owner: string, domainSlug: string) {
        return this.http.get<EventTagListResponse>(
            `/domain/${owner}/${domainSlug}/eventTag/list`
        ).pipe(
            map(response => ({
                eventTagList: response.eventTagDTOList.map(
                    x => this.mapEventTag(x)
                )
            }))
        )
    }

    private mapEventTag(dto: eventTagDTO) {
        return {
            eventTagId: dto.eventTagId,
            name: dto.name,
            description: dto.description,
            eventTagParentId: dto.parentEventTagId
        }
    }
}