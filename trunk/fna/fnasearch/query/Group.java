package org.fna.fnasearch.query;

import java.util.ArrayList;

public abstract class Group {
	public abstract boolean isComposite();
	public abstract ArrayList<Binding> getBindings();
	public abstract Group appendFilter(Filter f);
	public abstract String toString();
}
