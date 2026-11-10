package warnings.overrides;

/** Not emitted, so TypeScript sees it as unknown. */
public class PlainItem implements Item {
  @Override
  public String name() {
    return "";
  }
}
