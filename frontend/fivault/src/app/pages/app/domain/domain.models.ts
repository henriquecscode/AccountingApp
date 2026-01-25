export interface VisibleDomain {
  domain: Domain
  roleCode: string;
}

export interface Domain {
  owner: string;
  name: string;
  slug: string;
  description: string;
}

export interface AppUserDomainRole{
  name: string;
  roleCode: string;
}