/**
 * Copyright (C) 2011 Joseph Lehner <joseph.c.lehner@gmail.com>
 *
 * This file is part of RxDroid.
 *
 * RxDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RxDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RxDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package at.caspase.rxdroid.util;

import java.lang.reflect.Array;

/**
 * Helper class for writing hashCode methods.
 *
 * The design is essentially copied from the Android SDK's javadoc, which in
 * turn is based on <em>Effective Java</em> item 8.
 *
 * Usage example:
 * <pre>
 *
 * // ...
 * &#64;Override
 * public int hashCode()
 * {
 *     Hasher hasher = new Hasher();
 *
 *     hasher.hash(mMember1);
 *     hasher.hash(mMember2);
 *     // ...
 *     hasher.hash(mMemberX);
 *
 *     return hasher.getHashCode();
 * }
 * // ...
 * </pre>
 *
 * @author Joseph Lehner
 *
 */
public class Hasher
{
	private int mHash = 23;

	public void hash(boolean b) {
		mHash = term() + (b ? 1 : 0);
	}

	public void hash(char c) {
		hash((int) c);
	}

	public void hash(int i) {
		mHash = term() + i;
	}

	public void hash(long l) {
		mHash = term() + (int) (l ^ (l >>> 32));
	}

	public void hash(float f) {
		hash(Float.floatToIntBits(f));
	}

	public void hash(double d) {
		hash(Double.doubleToLongBits(d));
	}

	public void hash(Object o)
	{
		if(o == null)
			hash(0);
		else if(!o.getClass().isArray())
			hash(o.hashCode());
		else
		{
			final int length = Array.getLength(o);
			for(int i = 0; i != length; ++i)
				hash(Array.get(o, i));
		}
	}
	
	public int getHashCode() {
		return mHash;
	}

	private int term() {
		return PRIME * mHash;
	}

	private static final int PRIME = 37;
}
