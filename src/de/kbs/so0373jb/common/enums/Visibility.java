package de.kbs.so0373jb.common.enums;

public enum Visibility {

	visibility_private, visibility_public, visibility_protected, visibility_package;
	
	@Override
	public String toString () {
		return name().substring(11);
	}
}
