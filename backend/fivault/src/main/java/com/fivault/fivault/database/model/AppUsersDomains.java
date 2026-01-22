package com.fivault.fivault.database.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users_domains")
public class AppUsersDomains {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_domain_id")
    private Long appUserDomainId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_role_id", nullable = false)
    private DomainRole domainRole;

    public Long getAppUserDomainId() {
        return appUserDomainId;
    }

    public void setAppUserDomainId(Long appUserDomainId) {
        this.appUserDomainId = appUserDomainId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public DomainRole getDomainRole() {
        return domainRole;
    }

    public void setDomainRole(DomainRole domainRole) {
        this.domainRole = domainRole;
    }
}
