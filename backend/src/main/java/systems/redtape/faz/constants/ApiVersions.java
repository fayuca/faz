package systems.redtape.faz.constants;

import java.util.List;

/** API contract version labels (path prefix + manifest). */
public final class ApiVersions {
	public static final String V1 = "v1";
	public static final String V2 = "v2";
	public static final String MANIFEST_CONTRACT = V1;
	public static final List<String> V1_AND_V2 = List.of(V1, V2);

	private ApiVersions() {
	}
}
