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

	public void setIsDefined(boolean isDefined)
		{ this.isDefined = isDefined; }


	// Other methods


}