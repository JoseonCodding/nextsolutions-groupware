import MoreButton from '../components/common/button/MoreButton';

const SectionWithMore = ({ icon: Icon, title, moreHref }) => (
  <div className="flex justify-between">
    <div className="flex">
      <Icon size="24px" />
      <h3 className="text-gray-500">{title}</h3>
    </div>
    <MoreButton href={moreHref} />
  </div>
);

export default SectionWithMore;
