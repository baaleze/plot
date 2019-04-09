package plot;

public enum RelationType {
    LOVER, MARRIAGE, SUPERIOR, MENTOR, KILLED_RELATIVE, HATE, FRIEND, STOLE, ACCOMPLICE, COLLEAGUE, FAMILY, ATTACKED_ME;

    public boolean isRelative() {
        return this == LOVER || this == MARRIAGE || this == FRIEND || this == FAMILY || this == MENTOR;
    }
}
