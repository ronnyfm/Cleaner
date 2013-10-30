package parce.cl.subaru;

public class ContainerComponent {
	private String name;
	private String path;
	private String type;
	private boolean inUse;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	public boolean isInUse() {
		return inUse;
	}
	
	public ContainerComponent(String name, String path){
		this.name = name;
		this.path = path;
		this.inUse = false;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
