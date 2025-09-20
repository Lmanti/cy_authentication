package co.com.crediya.cy_authentication.api.config;

public enum RolesEnum {
    ADMIN("ADMIN", 1),
    ASESOR("ASESOR", 2),
    CLIENTE("CLIENTE", 3);

    private String value;
    private int id;

    RolesEnum(String value, int id) {
        this.value = value;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public int getId() {
        return id;
    }

    public static String getRoleById(int roleId) {
         for (RolesEnum role : RolesEnum.values()) {
            if (role.getId() == roleId) {
                return String.valueOf(role.value);
            }
        }
        return null;
    }
}
