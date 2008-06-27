package flapjack.data;

public class TraitValue extends XMLRoot
{
	// A reference to the trait that this value corresponds to
	private Trait trait;

	// The actual value itself
	private float value;

	// Some lines may need "dummy" trait data
	private boolean isDefined;

	// XML (Castor) constructor
	public TraitValue()
	{
	}

	/** Constructs a new trait value that is undefined (no data). */
	public TraitValue(Trait trait)
	{
		this.trait = trait;

		isDefined = false;
	}

	/** Constucts a new trait value for the trait and value provided. */
	public TraitValue(Trait trait, float value)
	{
		this.trait = trait;
		this.value = value;

		isDefined = true;
	}

	void validate()
		throws NullPointerException
	{
		if (trait == null)
			throw new NullPointerException();
	}


	// Methods required for XML serialization

	public Trait getTrait()
		{ return trait; }

	public void setTrait(Trait trait)
		{ this.trait = trait; }

	public float getValue()
		{ return value; }

	public void setValue(float value)
		{ this.value = value; }

	public boolean isDefined()
		{ return isDefined; }

	public void setDefined(boolean isDefined)
		{ this.isDefined = isDefined; }


	// Other methods

	/**
	 * Compares this trait value to another trait value (that MUST be a value
	 * for the same trait, otherwise the result will be undefined).
	 */
	public int compareTo(TraitValue other)
	{
		// To begin with, are the two trait values even comparable?
		if (!isDefined)
			return -1;
		if (!other.isDefined)
			return 1;

		// Numerical comparisons are easy...
		if (trait.traitIsNumerical())
		{
			if (value < other.value)
				return -1;
			else if (value == other.value)
				return 0;
			else
				return 1;
		}

		// But for categorical, we (unfortunately) need to do string comparisons
		// rather than the lookup value, otherwise the user won't neccassarily
		// see the same result as they would in Excel (eg, if "male", "female"
		// are the categories with lookup values 0 and 1, male will sort before
		// female (m before f) which is wrong).
		else
		{
			String myCat = trait.format(this);
			String otherCat = trait.format(other);

			return myCat.compareTo(otherCat);
		}
	}
}