# Many to many
@ManyToMany
@JoinTable(
    name = "app_users_domains",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "domain_id")
)
private Set<Domain> domains;
