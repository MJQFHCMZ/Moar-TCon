package com.existingeevee.moretcon.traits.book;

import java.util.List;

import com.existingeevee.moretcon.traits.modifiers.Shocked;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.sectiontransformer.SectionTransformer;
import slimeknights.tconstruct.library.modifiers.Modifier;

// adapted from Tinkers' MEMES BookTransformerAppendModifiers
public class BookTransformerAppendModifiers extends SectionTransformer {

	private final BookRepository source;
	private final boolean armor;
	private final List<Modifier> modCollector;

	public BookTransformerAppendModifiers(BookRepository source, boolean armor, List<Modifier> modCollector) {
		super("modifiers");
		this.source = source;
		this.modCollector = modCollector;
		this.armor = armor;
	}

	public BookTransformerAppendModifiers(BookRepository source, List<Modifier> modCollector) {
		this(source, false, modCollector);
	}

	@Override
	public void transform(BookData book, SectionData section) {
		ContentListing listing = (ContentListing) section.pages.get(0).content;
		for (Modifier mod : modCollector) {
			PageData page = new PageData();
			page.source = source;
			page.parent = section;
			page.data = "modifiers/" + mod.identifier.replaceFirst("moretcon.", "") + ".json";
			page.type = mod instanceof Shocked ? "lightning_modifier" : "modifier";
						
			if (armor) {
				page.data = "armor_" + page.data;
				page.type = "armor" + page.type;
			}
	
			section.pages.add(page);
			page.load();
			
//			System.out.println(new Gson().toJson(page));

			listing.addEntry(mod.getLocalizedName(), page);
		}
	}
}