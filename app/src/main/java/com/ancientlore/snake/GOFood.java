package com.ancientlore.snake;

import android.graphics.Rect;


class GOFood extends GameObject
{
	private int value;

	GOFood(int newIndex, String newName, int newX, int newY, Rect newRect)
	{
		super(newIndex, newName, newX, newY, newRect);
		setValue();
	}

	private void setValue()
	{
		switch (getName())
		{
			case "apple":
				value = 15;
				break;
			case "egg":
				value = 10;
				break;
		}
	}

	public int getValue()
	{
		return value;
	}
}
