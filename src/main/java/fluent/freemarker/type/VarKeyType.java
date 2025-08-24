package fluent.freemarker.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VarKeyType {

    private String varKey;

    private String varType;


    public static VarKeyType ofType(String varType) {
        return new VarKeyType(null, varType);
    }


    public static VarKeyType ofKeyType(String varKey, String varType) {
        return new VarKeyType(varKey, varType);
    }
}
