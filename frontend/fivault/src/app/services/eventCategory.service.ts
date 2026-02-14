import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { EventCreateRequest } from "./event.service";
import { map, Observable } from "rxjs";
import { EventCategory } from "../pages/app/domain/eventCategory/eventCategory.model";

interface eventCategoryDTO {
    eventCategoryId: string;
    name: string;
    description: string;
    parentEventCategoryId: string | any

}

interface EventCategoryCreateUpdateResult {
    eventCategory: EventCategory;
}
interface EventCategoryCreateUpdateResponse {
    eventCategoryDTO: eventCategoryDTO;
}

interface EventCategoryListResult {
    eventCategoryList: EventCategory[]
}
interface EventCategoryListResponse {
    eventCategoryDTOList: eventCategoryDTO[]
}
@Injectable({
    providedIn: 'root'
})
export class EventCategoryService {

    constructor(private http: HttpClient) { }

    create(owner: string, domainSlug: string, name: string, description: string, parentEventCategoryId: string | null): Observable<EventCategoryCreateUpdateResult> {
        return this.http.post<EventCategoryCreateUpdateResponse>(
            `/domain/${owner}/${domainSlug}/eventCategory/create`,
            {
                name,
                description,
                parentEventCategoryId: parentEventCategoryId
            }).pipe(
                map(response => ({
                    eventCategory: this.mapEventCategory(response.eventCategoryDTO)
                }))
            );
    }

    update(owner: string, domainSlug: string, eventCategoryId: string, name: string, description: string, parentEventCategoryId: string | null): Observable<EventCategoryCreateUpdateResult> {
        return this.http.post<EventCategoryCreateUpdateResponse>(
            `/domain/${owner}/${domainSlug}/eventCategory/update/${eventCategoryId}`,
            {
                name,
                description,
                parentEventCategoryId: parentEventCategoryId
            }).pipe(
                map(response => ({
                    eventCategory: this.mapEventCategory(response.eventCategoryDTO)
                }))
            );
    }

    getList(owner: string, domainSlug: string): Observable<EventCategoryListResult> {
        return this.http.get<EventCategoryListResponse>(
            `/domain/${owner}/${domainSlug}/eventCategory/list`
        ).pipe(
            map(response => ({
                eventCategoryList: response.eventCategoryDTOList.map(
                    x => this.mapEventCategory(x)
                )
            }))
        )
    }


    delete(owner: string, domainSlug: string, eventCategoryId: string): Observable<void> {
        return this.http.delete<void>(
            `/domain/${owner}/${domainSlug}/eventCategory/delete/${eventCategoryId}`
        );
    }

    private mapEventCategory(dto: eventCategoryDTO) {
        return {
            eventCategoryId: dto.eventCategoryId,
            name: dto.name,
            description: dto.description,
            parentEventCategoryId: dto.parentEventCategoryId
        }
    }
}